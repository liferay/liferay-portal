/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.security.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

/**
 * @author Pedro Leite
 */
public class WorkflowMetricsPermissionUtil {

	public static void check() throws PortalException {
		PortletPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			WorkflowPortletKeys.WORKFLOW_METRICS,
			ActionKeys.ACCESS_IN_CONTROL_PANEL);
	}

}